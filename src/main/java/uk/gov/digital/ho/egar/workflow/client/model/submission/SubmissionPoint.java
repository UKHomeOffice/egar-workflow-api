package uk.gov.digital.ho.egar.workflow.client.model.submission;


import uk.gov.digital.ho.egar.workflow.model.rest.Point;

/**
 * Extended to account for differences when submitting.
 */
public class SubmissionPoint extends Point{ 
	
	public static SubmissionPoint copy(Point src)
	{
		return (SubmissionPoint) src ;
	}
}