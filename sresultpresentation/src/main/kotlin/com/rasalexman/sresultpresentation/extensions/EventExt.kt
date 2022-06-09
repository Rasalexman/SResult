@file:Suppress("unused")
package com.rasalexman.sresultpresentation.extensions

import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresultpresentation.fragments.IBaseFragment
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel
import com.rasalexman.sresultpresentation.viewModels.IEventableViewModel


///------ INLINE SECTION ----///
inline fun <reified T : IBaseViewModel> IBaseFragment<T>.fetch() =
    this.viewModel?.processEvent(SEvent.Fetch)

fun <R : Any> IBaseFragment<IBaseViewModel>.fetchWith(data: R) =
    this.viewModel?.processEvent(SEvent.FetchWith(data))

inline fun <reified T : IBaseViewModel> IBaseFragment<T>.refresh() =
    this.viewModel?.processEvent(SEvent.Refresh)

fun IBaseFragment<IBaseViewModel>.validateData() =
    this.viewModel?.processEvent(SEvent.Validate)

inline fun <reified T : IBaseViewModel> IBaseFragment<T>.postEvent(event: ISEvent) =
    this.viewModel?.processEvent(event)

////-------- GLOBAL VM SECTION ----///
fun IEventableViewModel.fetch() =
    this.processEvent(SEvent.Fetch)

fun <R : Any> IEventableViewModel.fetchWith(data: R) =
    this.processEvent(SEvent.FetchWith(data))

fun IEventableViewModel.refresh() =
    this.processEvent(SEvent.Refresh)