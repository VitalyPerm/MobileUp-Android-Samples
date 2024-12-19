package ru.mobileup.samples.core.dialog.standard

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.samples.core.dialog.simple.SimpleDialogControl
import ru.mobileup.samples.core.dialog.simple.fakeSimpleDialogControl
import ru.mobileup.samples.core.dialog.simple.simpleDialogControl

typealias StandardDialogControl = SimpleDialogControl<StandardDialogData>

fun ComponentContext.standardDialogControl(
    key: String
): StandardDialogControl {
    return simpleDialogControl(
        key = key,
        dismissableByUser = { data -> data.dismissableByUser }
    )
}

fun fakeStandardDialogControl(data: StandardDialogData = StandardDialogData.MOCK): StandardDialogControl {
    return fakeSimpleDialogControl(data)
}