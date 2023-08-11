package com.example.cleanarchitecturewithtextfield.domain.use_case

import android.util.Patterns

class ValidateEmailUseCase {

    fun execute(email : String) : ValidationResult{
        //email이 null인 경우
        if(email.isBlank()){
            return ValidationResult(
                successful = false,
                errorMessage = "The email can't be black"
            )
        }

        //email 형식에 맞지 않는 경우
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return ValidationResult(
                successful = false,
                errorMessage = "That's not a valid email"
            )
        }

        //email success인 경우
        return ValidationResult(
            successful = true
        )
    }


}