package uk.gov.digital.ho.egar.workflow.client.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.workflow.client.model.ClientSubmission;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionAircraft;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionAttribute;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionFiles;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionLocation;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionPeopleSummary;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionPerson;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionPersonDetails;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionPersonType;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionPoint;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionResponsiblePersonDetails;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionResponsiblePersonType;
import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;
import uk.gov.digital.ho.egar.workflow.model.rest.FileStatus;
import uk.gov.digital.ho.egar.workflow.model.rest.PersonDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.Point;
import uk.gov.digital.ho.egar.workflow.model.rest.ResponsiblePersonDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.ResponsiblePersonType;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AircraftWithId;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FileWithIdResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSummary;
import uk.gov.digital.ho.egar.workflow.model.rest.response.LocationWithId;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PeopleSummary;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PersonWithIdResponse;

@Component
public class GarSummaryToClientSubmissionConverter implements Converter<GarSummary, ClientSubmission> {

	@Override
	public ClientSubmission convert(final GarSummary source) {
		/*
		 * Converting aircraft
		 */
		SubmissionAircraft targetAircraft = null;
		if (source.getAircraft() != null) {
			targetAircraft = aircraftConverter(source.getAircraft());
		}
		
		/*
		 * Converting location
		 */
		//FIXME: should we guard the for loops on locations, crew and passengers if there arent any? (Think they always exist)
		List<LocationWithId> sourceLocation = source.getLocation();
		List<SubmissionLocation> targetLocation = new ArrayList<>();
		for (LocationWithId oneLocation : sourceLocation) {
			if (oneLocation != null) {
				SubmissionLocation location = locationConverter(oneLocation);
				targetLocation.add(location);
			} else {
				targetLocation.add(null);
			}

		}
		/*
		 * Converting people
		 */
		PeopleSummary sourcePeople = source.getPeople();
		SubmissionPeopleSummary targetPeople = new SubmissionPeopleSummary();
		SubmissionPerson targetCaptain = null;
		if (sourcePeople.getCaptain() != null) {
			targetCaptain = personConverter(sourcePeople.getCaptain());
		}
		
		//FIXME: should we guard the for loops on locations, crew and passengers if there arent any? (Think they always exist)
		List<PersonWithIdResponse> sourceCrew = sourcePeople.getCrew();
		List<SubmissionPerson> targetCrew = new ArrayList<>();
		for (PersonWithIdResponse crew : sourceCrew) {
			if (crew != null) {
				SubmissionPerson newCrew = personConverter(crew);
				targetCrew.add(newCrew);
			} else {
				targetCrew.add(null);
			}
		}

		//FIXME: should we guard the for loops on locations, crew and passengers if there arent any? (Think they always exist)
		List<PersonWithIdResponse> sourcePassengers = sourcePeople.getPassengers();
		List<SubmissionPerson> targetPassengers = new ArrayList<>();
		for (PersonWithIdResponse passenger : sourcePassengers) {
			if (passenger != null) {
				SubmissionPerson newPassenger = personConverter(passenger);
				targetPassengers.add(newPassenger);
			} else {
				targetPassengers.add(null);
			}

		}

		targetPeople.setCaptain(targetCaptain);
		targetPeople.setCrew(targetCrew);
		targetPeople.setPassengers(targetPassengers);

		/*
		 * Converting files //FIXME not yet implemented
		 */

		List<SubmissionFiles> targetFiles = source.getFiles().stream()
				.filter(Objects::nonNull)
				.map(file->fileConverter(file))
				.collect(Collectors.toList());
	

		/*
		 * Converting attributes
		 */
		SubmissionAttribute targetAttributes = null;
		if (source.getAttributes() != null) {
			targetAttributes = attributeConverter(source.getAttributes());
		}
		/*
		 * setting clientSubmission fields
		 */

		ClientSubmission target = new ClientSubmission();
		target.setGarUuid(source.getGarUuid());
		target.setAircraft(targetAircraft);
		target.setLocation(targetLocation);
		target.setPeople(targetPeople);
		target.setFiles(targetFiles);
		target.setAttributes(targetAttributes);

		return target;
	}
	/*
	 * CONVERTERS
	 */

	private SubmissionAircraft aircraftConverter(final AircraftWithId sourceAircraft) {

		SubmissionAircraft targetAircraft = new SubmissionAircraft();
		targetAircraft.setBase(sourceAircraft.getBase());
		targetAircraft.setRegistration(sourceAircraft.getRegistration());
		targetAircraft.setTaxesPaid(sourceAircraft.getTaxesPaid());
		targetAircraft.setType(sourceAircraft.getType());
		return targetAircraft;
	}

	private SubmissionLocation locationConverter(final LocationWithId location) {

		SubmissionLocation newLocation = new SubmissionLocation();
		newLocation.setDateTime(location.getDateTime());
		newLocation.setIcaoCode(location.getIcaoCode());
		newLocation.setLegCount(location.getLegCount());
		newLocation.setLegNo(location.getLegNo());
		newLocation.setIataCode(location.getIataCode());

		Point sourcePoint = location.getPoint();
		SubmissionPoint targetPoint = new SubmissionPoint();
		targetPoint.setLatitude(sourcePoint.getLatitude());
		targetPoint.setLongitude(sourcePoint.getLongitude());

		newLocation.setPoint(targetPoint);

		return newLocation;
	}

	private SubmissionPerson personConverter(final PersonWithIdResponse sourcePerson) {

		PersonDetails innerSourcePerson = sourcePerson.getPersonDetails();

		SubmissionPerson targetPerson = new SubmissionPerson();
		SubmissionPersonDetails innerTarget = new SubmissionPersonDetails();

		innerTarget.setAddress(innerSourcePerson.getAddress());
		innerTarget.setAddress(innerSourcePerson.getAddress());
		innerTarget.setDob(innerSourcePerson.getDob());
		innerTarget.setDocumentCountryCode(innerSourcePerson.getDocumentCountryCode());
		innerTarget.setDocumentExpiryDate(innerSourcePerson.getDocumentExpiryDate());
		innerTarget.setDocumentNo(innerSourcePerson.getDocumentNo());
		innerTarget.setDocumentType(innerSourcePerson.getDocumentType());
		innerTarget.setFamilyName(innerSourcePerson.getFamilyName());
		innerTarget.setGivenName(innerSourcePerson.getGivenName());
		innerTarget.setGender(innerSourcePerson.getGender());
		innerTarget.setNationality(innerSourcePerson.getNationality());
		innerTarget.setPlace(innerSourcePerson.getPlace());

		targetPerson.setPersonDetails(innerTarget);
		targetPerson.setType(SubmissionPersonType.valueOf(sourcePerson.getType().name()));

		return targetPerson;
	}

	private SubmissionFiles fileConverter(FileWithIdResponse source) {
		SubmissionFiles target = new SubmissionFiles();
		
		target.setFileLink(source.getFileLink());
		target.setFileName(source.getFileName());
		target.setFileUuid(source.getFileUuid());
		target.setUploadComplete(source.getFileStatus() == FileStatus.VIRUS_SCANNED);

		return target;

	}

	private SubmissionAttribute attributeConverter(final Attribute sourceAttributes) {

		SubmissionAttribute targetAttributes = new SubmissionAttribute();
		SubmissionResponsiblePersonDetails targetDetails = new SubmissionResponsiblePersonDetails();

		ResponsiblePersonDetails sourceDetails = sourceAttributes.getOtherDetails();
		if (sourceDetails != null) {
			ResponsiblePersonType sourceResponsible = sourceDetails.getType();
			if (sourceResponsible != null) {
				targetDetails.setType(SubmissionResponsiblePersonType.valueOf(sourceResponsible.name()));
			}
			targetDetails.setContactNumber(sourceDetails.getContactNumber());
			targetDetails.setName(sourceDetails.getName());
			targetDetails.setAddress(sourceDetails.getAddress());
		}

		targetAttributes.setHazardous(sourceAttributes.getHazardous());
		targetAttributes.setOtherDetails(targetDetails);

		return targetAttributes;

	}

}
