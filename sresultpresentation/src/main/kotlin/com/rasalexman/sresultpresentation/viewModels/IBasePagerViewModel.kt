package com.rasalexman.sresultpresentation.viewModels

interface IBasePagerViewModel : IEventableViewModel {
    var items: List<IEventableViewModel>
}