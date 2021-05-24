package com.rasalexman.sresultpresentation.extensions

import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresultpresentation.fragments.IBaseFragment
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel


///------ INLINE SECTION ----///
inline fun <reified T : IBaseViewModel> IBaseFragment<T>.fetch() =
    this.viewModel?.processViewEvent(SEvent.Fetch)

fun <R : Any> IBaseFragment<IBaseViewModel>.fetchWith(data: R) =
    this.viewModel?.processViewEvent(SEvent.FetchWith(data))

inline fun <reified T : IBaseViewModel> IBaseFragment<T>.refresh() =
    this.viewModel?.processViewEvent(SEvent.Refresh)

fun IBaseFragment<IBaseViewModel>.validateData() =
    this.viewModel?.processViewEvent(SEvent.Validate)