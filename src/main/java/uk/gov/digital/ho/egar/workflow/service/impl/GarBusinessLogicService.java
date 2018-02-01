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
import uk.gov.digital.ho.egar.workflow.model.rest.response.*;
import uk.gov.digital.ho.egar.workflow.service.GarService;
import uk.gov.digital.ho.egar.workflow.service.behaviour.GarChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
	public GarListResponse getAllGars(final AuthValues authValues)throws WorkflowException {
		//No business logic required.
		return garClient.getListOfGars(authValues);
	}

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
