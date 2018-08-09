package gov.pnnl.proven.cluster.module;


@ProvenService
public class ExplicitDisclosureService implements ModuleService<ExplicitDisclosureRequest,DisclosureResponseRequest>
{

	
	
	@Override
	public DisclosureResponseRequest submit(ExplicitDisclosureRequest t) {
		System.out.println("Inside submit ExplistDisclosureRequest");
		return null;
	}
	
}
