package co.edu.uco.easypark.features.auth.application;

public interface IAuthUseCase {

    LoginResponse login(LoginRequest request);

    LoginResponse register(RegisterRequest request);
}