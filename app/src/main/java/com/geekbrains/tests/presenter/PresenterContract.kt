package com.geekbrains.tests.presenter

internal interface PresenterContract<ViewContract> {
    fun onAttach(viewContract: ViewContract)
    fun onDetach()
}
