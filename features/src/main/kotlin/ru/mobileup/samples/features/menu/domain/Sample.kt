package ru.mobileup.samples.features.menu.domain

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.strResDesc
import ru.mobileup.samples.core.domain.DisplayedEnum
import ru.mobileup.samples.features.R

enum class Sample(
    override val displayName: StringDesc
) : DisplayedEnum {

    Form(R.string.menu_item_form.strResDesc()),
    Otp(R.string.menu_item_otp.strResDesc()),
    Video(R.string.menu_item_video.strResDesc()),
    Calendar(R.string.menu_item_calendar.strResDesc()),
    QrCode(R.string.menu_item_qr_code.strResDesc()),
    Chart(R.string.menu_item_chart.strResDesc()),
    Navigation(R.string.menu_item_navigation.strResDesc()),
    CollapsingToolbar(R.string.menu_item_collapsing_toolbar.strResDesc()),
    Image(R.string.menu_item_image.strResDesc()),
    Tutorial(R.string.menu_item_tutorial.strResDesc()),
    YandexMap(R.string.menu_item_yandex_map.strResDesc())
}
