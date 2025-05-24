package com.github.mallowigi.treehighlighter

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.service

@Service(Service.Level.APP)
class ProjectTreeConfigState : SimplePersistentStateComponent<ProjectTreeConfigState.State>(State()) {
  class State : BaseState() {
    var marksForCollapsedHighlights: String? by string("")
  }

  var marksForCollapsedHighlights: String
    get() = state.marksForCollapsedHighlights ?: ""
    set(value) {
      state.marksForCollapsedHighlights = value
    }

  fun apply(state: ProjectTreeConfigState) {
    this.marksForCollapsedHighlights = state.marksForCollapsedHighlights
  }

  fun reset() {
    this.marksForCollapsedHighlights = ""
  }

  fun clone(): ProjectTreeConfigState {
    val clone = ProjectTreeConfigState()
    clone.marksForCollapsedHighlights = this.marksForCollapsedHighlights
    return clone
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ProjectTreeConfigState) return false
    return marksForCollapsedHighlights == other.marksForCollapsedHighlights
  }

  override fun hashCode(): Int {
    return marksForCollapsedHighlights.hashCode()
  }

  companion object {
    @JvmStatic
    val instance: ProjectTreeConfigState
      get() = service()
  }
}