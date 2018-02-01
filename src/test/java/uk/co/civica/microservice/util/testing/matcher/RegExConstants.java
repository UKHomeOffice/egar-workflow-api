package uk.co.civica.microservice.util.testing.matcher;

public interface RegExConstants {

	public final String REGEX_UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
	public final String REGEX_DATE = "(19|20)[0-9][0-9]-(0[0-9]|1[0-2])-(0[1-9]|([12][0-9]|3[01]))T([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]";
	public final String REGEX_ZULU_DATETIME = "(19|20)[0-9][0-9]-(0[0-9]|1[0-2])-(0[1-9]|([12][0-9]|3[01]))T([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]Z";
	public final String REGEX_X_CODE = "[a-zA-Z0-9]{5}";
	public final String REGEX_ICAO_CODE = "[0-9a-zA-Z\\-]{1,13}";
	public final String REGEX_COUNTRY_CODE = "[A-Z0-9]{1,3}";
	public final String REGEX_P0STCODE = "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}$";
	public final String REGEX_DOB ="(19|20)[0-9][0-9]-(0[0-9]|1[0-2])-(0[1-9]|([12][0-9]|3[01]))";
	public final String REGEX_CONTACT = "[0-9]{11}";
	public final String REGEX_RESPONSIBLE_PERSON_TYPE = "(OTHER|CAPTAIN)";
	public final String REGEX_PERSON_TYPE = "(CAPTAIN|CREW|PASSENGER)";
	public final String REGEX_GENDER = "(MALE|FEMALE|OTHER)";
	public final String REGEX_SUBMISSION_TYPE = "(CBP_STT)";
}
