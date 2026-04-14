package group1.be_mes_project.dto;

public record ApiResponse<T>(boolean success, T data, String message) {

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, data, null);
  }

  public static <T> ApiResponse<T> fail(String message) {
    return new ApiResponse<>(false, null, message);
  }
}

