package com.example.cleanarchitecturewithtextfield.domain.use_case

import android.util.Patterns

class ValidatePasswordUseCase {

    fun execute(password : String) : ValidationResult{
        //password가 8자리 이하인 경우
        if(password.length < 8){
            return ValidationResult(
                successful = false,
                errorMessage = "The password needs to consist of at least 8 characters"
            )
        }

        //password 형식에 맞지 않는 경우
        val containsLetterAndDigits = password.any{ it.isDigit()} &&
                password.any{ it.isLetter() }
        if(!containsLetterAndDigits){
            return ValidationResult(
                successful = false,
                errorMessage = "The password needs to contain at least one letter and digit"
            )
        }

        //password success인 경우
        return ValidationResult(
            successful = true
        )
    }


}