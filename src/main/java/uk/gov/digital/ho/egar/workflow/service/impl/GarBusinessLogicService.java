/**
 *
 */
package uk.gov.digital.ho.egar.workflow.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.*;
import uk.gov.digital.ho.egar.workflow.model.rest.PersonType;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.GarBulkSummaryResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.GarList;
import uk.gov.digital.ho.egar.workflow.model.rest.response.*;
import uk.gov.digital.ho.egar.workflow.service.GarService;
import uk.gov.digital.ho.egar.workflow.service.behaviour.GarChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Pulls all the services together into a business process.
 */
@Service
@Primary
public class GarBusinessLogicService implements GarService {

	@Autowired
	private GarClient garClient;

	@Autowired
	private AircraftClient aircraftClient;

	@Autowired
	private LocationClient locationClient;

	@Autowired
	private PersonClient personClient;

	@Autowired
	private FileClient fileClient;

	@Autowired
	private SubmissionClient submissionClient;

	@Autowired
	private GarChecker behaviourChecker;

	@Autowired
	private ConversionService conversionService;

	@Override
	public UUID createGar(final AuthValues authValues) throws WorkflowException {
		GarSkeleton gar = garClient.createGar(authValues);

		return gar.getGarUuid();
	}

	@Override
	public GarSummary getGarSummary(final AuthValues authValues,UUID garUuid) throws WorkflowException {

		GarSkeleton gar = garClient.getGar(authValues, garUuid);

		behaviourChecker.checkGarExists(gar, garUuid);

		AircraftWithId aircraft = null;
		if (gar.getAircraftId() != null) {
			aircraft = aircraftClient.retrieveAircraft(authValues, gar.getAircraftId());
		}

		AttributeResponse attribute = null;
		if (gar.getAttributes() != false) {
			attribute = conversionService.convert(gar, AttributeResponse.class);

		}

		List<LocationWithId> locations = new ArrayList<>();
		if (gar.getLocationIds() != null) {
			for (UUID locationId : gar.getLocationIds()) {
				locations.add(locationClient.retrieveLocation(authValues, locationId));
			}
		}

		PersonWithIdResponse captain = null;
		List<PersonWithIdResponse> crew = new ArrayList<>();
		List<PersonWithIdResponse> passengers = new ArrayList<>();
		if (gar.getPeople() != null) {
			PeopleSkeletonDetailsResponse people = gar.getPeople();
			if (people.getCaptain() != null) {
				captain = personClient.retrievePerson(authValues, people.getCaptain());
				captain.setType(PersonType.CAPTAIN);
			}
			if (people.getPassengers() != null) {
				for (UUID personId : people.getPassengers()) {
					PersonWithIdResponse passenger = personClient.retrievePerson(authValues, personId);
					passenger.setType(PersonType.PASSENGER);
					passengers.add(passenger);
				}
			}
			if (people.getCrew() != null) {
				for (UUID personId : people.getCrew()) {
					PersonWithIdResponse crewMember = personClient.retrievePerson(authValues, personId);
					crewMember.setType(PersonType.CREW);
					crew.add(crewMember);
				}
			}
		}

		List<FileWithIdResponse> files = new ArrayList<>();
		if (gar.getFileIds() != null) {
			for (UUID fileUuid : gar.getFileIds()) {
				files.add(fileClient.retrieveFileDetails(authValues, fileUuid));
			}
		}
		SubmissionGar submission = null;
		if (gar.getSubmission() != false) {
			submission = submissionClient.getSubmission(authValues, gar.getSubmissionId());

		}

		return composeSummaryResponse(gar, aircraft, attribute, locations, captain, crew, passengers, files, submission);

	}

	@Override
	public GarSkeleton getGar(final AuthValues authValues,final UUID garUuid) throws WorkflowException {

		GarSkeleton gar = garClient.getGar(authValues,garUuid);

		behaviourChecker.checkGarExists(gar, garUuid);

		return gar;
	}

	@Override
	public GarList getAllGars(final AuthValues authValues)throws WorkflowException {
		//No business logic required.
		return garClient.getListOfGars(authValues);
	}
	@Override
	public GarBulkSummaryResponse getBulkGars(AuthValues authValues, List<UUID> garList) throws WorkflowException {
		/*-----------------------------------------------------------------------------------------------------------------------
		 * Retrieves bulk gar skeletons from list of gar uuids and then separates them into lists
		 */
		List<GarSkeleton> garSkeletons = garClient.getBulk(authValues,garList);

		List<UUID> aircraftUuids = garSkeletons.stream()
				.map(GarSkeleton::getAircraftId)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		List<UUID> submissionUuids = garSkeletons.stream()
				.map(GarSkeleton::getSubmissionId)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		List<UUID> locationUuids = garSkeletons.stream()
				.flatMap(skel->skel.getLocationIds().stream())
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		List<UUID> fileUuids = garSkeletons.stream()
				.flatMap(skel->skel.getFileIds().stream())
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		List<UUID> peopleUuids = garSkeletons.stream()
				.flatMap(skel->skel.getPeople().getPeopleUuids().stream())
				.filter(Objects::nonNull)
				.collect(Collectors.toList());    

		/*-----------------------------------------------------------------------------------------------------------------------
		 * Bulk fetch of all the data from individual micro services
		 */    
		List<AircraftWithId> aircraftsList = aircraftClient.getBulk(authValues, aircraftUuids);
		List<SubmissionGar> submissionsList = submissionClient.getBulk(authValues, submissionUuids);
		List<LocationWithId> locationsList = locationClient.getBulk(authValues, locationUuids);
		List<FileWithIdResponse> filesList = fileClient.getBulk(authValues, fileUuids);
		List<PersonWithIdResponse> peopleList = personClient.getBulk(authValues, peopleUuids);
		/*-----------------------------------------------------------------------------------------------------------------------
		 * Composing each gar summary from bulk data
		 */  
		List<GarSummary> garSummaries = new ArrayList<>();

		for(GarSkeleton garSkeleton: garSkeletons){

			AircraftWithId aircraft = retrieveMatchingAircraft(aircraftsList, garSkeleton.getAircraftId());

			AttributeResponse attribute = null;
			if (garSkeleton.getAttributes() != false) {
				attribute = conversionService.convert(garSkeleton, AttributeResponse.class);
			}

			List<LocationWithId> locations = new ArrayList<>();
			for (UUID locationId : garSkeleton.getLocationIds()) {
				locations.add(retrieveMatchingLocation(locationsList, locationId));

			}

			PersonWithIdResponse captain = null;
			List<PersonWithIdResponse> crew = new ArrayList<>();
			List<PersonWithIdResponse> passengers = new ArrayList<>();

			PeopleSkeletonDetailsResponse people = garSkeleton.getPeople();

			captain = retrieveMatchingPerson(peopleList, people.getCaptain());
			if(people.getCaptain() != null)captain.setType(PersonType.CAPTAIN);

			if(people.getCrew() != null){
				for (UUID crewUuid : people.getCrew()) {
					PersonWithIdResponse crewMember = retrieveMatchingPerson(peopleList, crewUuid);
					crewMember.setType(PersonType.CREW);
					crew.add(crewMember);
				}
			}

			if(people.getPassengers() != null){
				for (UUID passengerUuid : people.getPassengers()) {
					PersonWithIdResponse passenger = retrieveMatchingPerson(peopleList, passengerUuid);
					passenger.setType(PersonType.PASSENGER);
					passengers.add(passenger);
				}
			}

			List<FileWithIdResponse> files = new ArrayList<>();
			for (UUID fileUuid : garSkeleton.getFileIds()) {
				files.add(retrieveMatchingFile(filesList, fileUuid));
			}

			SubmissionGar submission = retrieveMatchingSubmission(submissionsList, garSkeleton.getSubmissionId());

			GarSummary summary =composeSummaryResponse(garSkeleton, aircraft, attribute, locations, captain, crew, passengers, files, submission);

			garSummaries.add(summary);

		}
		GarBulkSummaryResponse response = new GarBulkSummaryResponse();
		response.setGars(garSummaries);
		return response;
	}

	/*-----------------------------------------------------------------------------------------------------------------------
	 * These methods map retrieved details to individual uuids
	 */
	private AircraftWithId retrieveMatchingAircraft(List<AircraftWithId> aircraftsList, UUID aircraftUuid) {
		return aircraftUuid == null ? null : aircraftsList.stream()
				.filter(key-> aircraftUuid.equals(key.getAircraftUuid()))
				.findFirst()
				.orElse(null);
	}

	private LocationWithId retrieveMatchingLocation(List<LocationWithId> locationsList, UUID locationUuid) {
		return locationUuid == null ? null : locationsList.stream()
				.filter(key-> locationUuid.equals(key.getLocationId()))
				.findFirst()
				.orElse(null);
	}

	private PersonWithIdResponse retrieveMatchingPerson(List<PersonWithIdResponse> peopleList, UUID personUuid) {
		return personUuid == null ? null : peopleList.stream()
				.filter(key-> personUuid.equals(key.getPersonId()))
				.findFirst()
				.orElse(null);
	}

	private FileWithIdResponse retrieveMatchingFile(List<FileWithIdResponse> filesList, UUID fileUuid) {
		return fileUuid == null ? null : filesList.stream()
				.filter(key-> fileUuid.equals(key.getFileUuid()))
				.findFirst()
				.orElse(null);
	}

	private SubmissionGar retrieveMatchingSubmission(List<SubmissionGar> submissionsList,  UUID submissionUuid) {
		return submissionUuid == null ? null : submissionsList.stream()
				.filter(key-> submissionUuid.equals(key.getSubmission().getSubmissionUuid()))
				.findFirst()
				.orElse(null);
	}

	/*-------------------------------------------------------------------------------------------------------------------------
	 * Composing a gar summary response
	 */

	private GarSummary composeSummaryResponse(GarSkeleton gar, 
			AircraftWithId aircraft, 
			AttributeResponse attribute, 
			List<LocationWithId> locations, 
			PersonWithIdResponse captain, 
			List<PersonWithIdResponse> crew, 
			List<PersonWithIdResponse> passengers,
			List<FileWithIdResponse> files, 
			SubmissionGar submission ) {
		GarSummary response = new GarSummary();

		response.setGarUuid(gar.getGarUuid());
		response.setUserUuid(gar.getUserUuid());

		response.setAircraft(aircraft);

		if(attribute != null){
			response.setAttributes(attribute.getAttribute());
		}
		response.setLocation(locations);

		PeopleSummary summaryResponse = new PeopleSummary();
		summaryResponse.setCaptain(captain);

		summaryResponse.setCrew(crew);

		summaryResponse.setPassengers(passengers);

		response.setPeople(summaryResponse);

		response.setFiles(files);

		if(submission != null){
			response.setSubmission(submission.getSubmission());
		}
		return response;
	}
}
