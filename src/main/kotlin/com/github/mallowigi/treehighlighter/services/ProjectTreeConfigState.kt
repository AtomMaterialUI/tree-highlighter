package com.github.mallowigi.treehighlighter.services

import com.github.mallowigi.treehighlighter.models.ColorSettings
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.XCollection

@Service(Service.Level.PROJECT)
internal class ProjectTreeConfigState : SimplePersistentStateComponent<ProjectTreeConfigState.State>(State()) {
  class State : BaseState() {
    @get:XCollection
    var colorList: MutableList<ColorSettings> by list()
  }

  var colorList: MutableList<ColorSettings>
    get() = state.colorList
    set(value) {
      state.colorList = value
    }

  fun apply(state: AppTreeConfigState) {
    this.colorList.clear()
    this.colorList.addAll(state.colorList)
  }

  fun reset() {
    this.colorList.clear()
  }

  fun clone(): AppTreeConfigState {
    val clone = AppTreeConfigState()
    clone.colorList.addAll(this.colorList)
    return clone
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is AppTreeConfigState) return false
    return colorList == other.colorList
  }

  override fun hashCode(): Int {
    val result = colorList.hashCode()
    return result
  }

  companion object {
    @JvmStatic
    fun getInstance(project: Project): ProjectTreeConfigState {
      return project.service<ProjectTreeConfigState>()
    }
  }
}