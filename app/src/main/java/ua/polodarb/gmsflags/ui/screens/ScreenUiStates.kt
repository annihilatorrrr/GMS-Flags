package ua.polodarb.gmsflags.ui.screens

sealed interface ScreenUiStates {
    data object Loading : ScreenUiStates

    data class Success(
        val data: List<String>
    ) : ScreenUiStates

    data class Error(
        val throwable: Throwable? = null
    ) : ScreenUiStates
}