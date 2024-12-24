package ru.mobileup.samples.features.form.presentation

import ru.mobileup.samples.core.utils.fakeInputControl

class FakeFormComponent : FormComponent {
    override val phoneInputControl = fakeInputControl()
}
