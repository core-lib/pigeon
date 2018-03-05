package payne.framework.pigeon.generation.async;

import payne.framework.pigeon.core.Callback;
import payne.framework.pigeon.core.OnCompleted;
import payne.framework.pigeon.core.OnFail;
import payne.framework.pigeon.core.OnSuccess;
import payne.framework.pigeon.core.annotation.Correspond;
import payne.framework.pigeon.core.annotation.Open;
import payne.framework.pigeon.generation.annotation.Name;
import payne.framework.pigeon.generation.async.SampleAPI;
import payne.framework.pigeon.generation.async.User;

@Correspond(SampleAPI.class)
@Open(value = "/sample")
public interface AsyncSampleAPI {

	@Open(value = "/login")
	 void login(@Name(value = "username") String username, @Name(value = "password") String password, Callback<User> callback);
	

	@Open(value = "/login")
	 void login(@Name(value = "username") String username, @Name(value = "password") String password, OnCompleted<User> onCompleted);


	@Open(value = "/login")
	 void login(@Name(value = "username") String username, @Name(value = "password") String password, OnSuccess<User> onSuccess);


	@Open(value = "/login")
	 void login(@Name(value = "username") String username, @Name(value = "password") String password, OnSuccess<User> onSuccess, OnFail onFail);


	@Open(value = "/login")
	 void login(@Name(value = "username") String username, @Name(value = "password") String password, OnSuccess<User> onSuccess, OnFail onFail, OnCompleted<User> onCompleted);

}
