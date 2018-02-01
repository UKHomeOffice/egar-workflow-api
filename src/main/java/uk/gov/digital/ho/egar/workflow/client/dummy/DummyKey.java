package uk.gov.digital.ho.egar.workflow.client.dummy;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
public class DummyKey {
	
	private UUID keyUuid;
	private UUID userUuid;
	
	public DummyKey(UUID keyUuid, UUID userUuid) {
		this.keyUuid = keyUuid;
		this.userUuid = userUuid;
	}
}
