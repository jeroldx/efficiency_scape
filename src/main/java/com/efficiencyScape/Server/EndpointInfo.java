package com.efficiencyScape.Server;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class EndpointInfo
{
	String path;
	EndpointTag endpointTag;
	@EqualsAndHashCode.Exclude
	boolean available;
}
