package payne.framework.pigeon.generation;

import payne.framework.pigeon.core.Document;
import payne.framework.pigeon.core.annotation.Open;
import payne.framework.pigeon.core.annotation.Param;

@Open("/generator")
public interface GeneratorService {

	@Open("/generate")
	Document generate(@Param("implementation") String implementation, @Param("interfase") String interfase) throws Exception;

}
