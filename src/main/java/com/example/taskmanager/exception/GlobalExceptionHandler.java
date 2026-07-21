package com.example.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
    /*
    GlobalExceptionHandler is a class used to handle errors for the entire REST API.

    @RestControllerAdvice tells Spring:
    "This class will listen for exceptions/errors that happen inside any controller."

    In our project, we use this class to handle validation errors.

    Validation errors happen when @Valid checks the rules in Task.java and the data is invalid.

    Example:
    If the user sends this JSON:

    {
        "title": "",
        "description": "This should fail",
        "completed": false
    }

    The title is invalid because Task.java has:

    @NotBlank(message = "Title is required")

    When validation fails, Spring throws a MethodArgumentNotValidException.

    Instead of returning a large default Spring error message, this handler creates a cleaner response.

    Example response:

    {
        "title": "Title is required"
    }

    This makes the API easier to understand for frontend developers or anyone using the API.
    */

@RestControllerAdvice
public class GlobalExceptionHandler {
    /*
    @ExceptionHandler(MethodArgumentNotValidException.class) tells Spring:
    "Run this method when a MethodArgumentNotValidException happens."

    MethodArgumentNotValidException happens when @Valid fails.

    @ResponseStatus(HttpStatus.BAD_REQUEST) tells Spring to return:

        400 Bad Request

    400 Bad Request means the client sent invalid data.
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException exception){
        /*
        This map will store the validation errors.

        The key is the field name.
        The value is the error message.

        Example:

        "title" -> "Title is required"
        */
        Map<String, String> errors = new HashMap<>();

         /*
        getBindingResult() gets the validation result from Spring.

        getFieldErrors() gets all validation errors related to fields.

        forEach loops through each error.

        error.getField() gets the field that failed validation.
        Example: "title"

        error.getDefaultMessage() gets the message from the annotation.
        Example: "Title is required"
        */
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        /*
        Returning the map makes Spring convert it into JSON automatically.
        */
        return errors;

    }


}
