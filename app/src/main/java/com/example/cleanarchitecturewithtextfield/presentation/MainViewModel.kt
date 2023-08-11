package com.example.cleanarchitecturewithtextfield.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecturewithtextfield.domain.use_case.ValidateEmailUseCase
import com.example.cleanarchitecturewithtextfield.domain.use_case.ValidatePasswordUseCase
import com.example.cleanarchitecturewithtextfield.domain.use_case.ValidateRepeatedPasswordUseCase
import com.example.cleanarchitecturewithtextfield.domain.use_case.ValidateTermsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val validateEmail : ValidateEmailUseCase = ValidateEmailUseCase(),
    private val validatePassword : ValidatePasswordUseCase = ValidatePasswordUseCase(),
    private val validateRepeatedpassword : ValidateRepeatedPasswordUseCase = ValidateRepeatedPasswordUseCase(),
    private val validateTerms : ValidateTermsUseCase = ValidateTermsUseCase()
) : ViewModel() {

    var state by mutableStateOf(RegistrationFormState())

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event : RegistrationFormEvent){
        when(event){
            is RegistrationFormEvent.EmailChanged -> {
                state = state.copy(email = event.email)
            }
            is RegistrationFormEvent.PasswordChanged -> {
                state = state.copy(password = event.password)
            }
            is RegistrationFormEvent.RepeatedPasswordChanged -> {
                state = state.copy(repeatedPassword = event.repeatedPassword)
            }
            is RegistrationFormEvent.AcceptTerms -> {
                state = state.copy(acceptedTerms = event.isAccepted)
            }
            is RegistrationFormEvent.Submit -> {
                submitData()
            }
        }
    }

    private fun submitData() {
        val emailResult = validateEmail.execute(state.email)
        val passwordResult = validatePassword.execute(state.password)
        val repeatedPasswordResult = validateRepeatedpassword.execute(
            state.password, state.repeatedPassword
        )
        val termsResult = validateTerms.execute(state.acceptedTerms)

        val hasError = listOf(
            emailResult,
            passwordResult,
            repeatedPasswordResult,
            termsResult
        ).any{ !it.successful }

        if(hasError){
            state = state.copy(
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage,
                repeatedPasswordError = repeatedPasswordResult.errorMessage,
                termsError = termsResult.errorMessage,
            )
            return
        }

        viewModelScope.launch{
            validationEventChannel.send(ValidationEvent.Success)
        }
    }

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }
}