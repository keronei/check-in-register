package com.keronei.domain.usecases

data class AttendanceUseCases(
    val checkInMemberUseCase: CheckInMemberUseCase,
    val listAttendeesUseCase: ListAttendeesUseCase,
    val undoCheckInUseCase: UndoCheckInUseCase,
)
