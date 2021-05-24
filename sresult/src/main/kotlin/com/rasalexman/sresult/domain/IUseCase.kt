package com.rasalexman.sresult.domain

interface IUseCase {
    interface SingleIn<in Input> : IUseCase {
        suspend operator fun invoke(data: Input)
    }

    interface DoubleInOut<in FirstInput, in SecondInput, out Output> : IUseCase {
        suspend operator fun invoke(firstParam: FirstInput, secondParam: SecondInput): Output
    }

    interface DoubleIn<in FirstInput, in SecondInput> : IUseCase {
        suspend operator fun invoke(firstParam: FirstInput, secondParam: SecondInput)
    }

    interface SingleInOut<in Input, out Output> : IUseCase {
        suspend operator fun invoke(data: Input): Output
    }

    interface Out<out Output> : IUseCase {
        suspend operator fun invoke(): Output
    }
}
