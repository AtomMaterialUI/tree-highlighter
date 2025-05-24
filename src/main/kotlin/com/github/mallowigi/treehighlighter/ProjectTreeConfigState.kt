package com.github.mallowigi.treehighlighter

import com.github.mallowigi.treehighlighter.models.ColorSettings
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.annotations.XCollection

@State(name = "ProjectTreeHighlighter", storages = [Storage("projectTreeHighlighter.xml")])
@Service(Service.Level.APP)
internal class ProjectTreeConfigState : SimplePersistentStateComponent<ProjectTreeConfigState.State>(State()) {
  class State : BaseState() {
    var marksForCollapsedHighlights: String? by string("")

    @get:XCollection
    var colorList: MutableList<ColorSettings> by list()
  }

  var marksForCollapsedHighlights: String
    get() = state.marksForCollapsedHighlights ?: ""
    set(value) {
      state.marksForCollapsedHighlights = value
    }

  var colorList: MutableList<ColorSettings>
    get() = state.colorList
    set(value) {
      state.colorList = value
    }

  fun apply(state: ProjectTreeConfigState) {
    this.marksForCollapsedHighlights = state.marksForCollapsedHighlights
    this.colorList.clear()
    this.colorList.addAll(state.colorList)
  }

  fun reset() {
    this.marksForCollapsedHighlights = ""
    this.colorList.clear()
  }

  fun clone(): ProjectTreeConfigState {
    val clone = ProjectTreeConfigState()
    clone.marksForCollapsedHighlights = this.marksForCollapsedHighlights
    clone.colorList.addAll(this.colorList)
    return clone
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ProjectTreeConfigState) return false
    return marksForCollapsedHighlights == other.marksForCollapsedHighlights &&
      colorList == other.colorList
  }

  override fun hashCode(): Int {
    var result = marksForCollapsedHighlights.hashCode()
    result = 31 * result + colorList.hashCode()
    return result
  }

  companion object {
    @JvmStatic
    val instance: ProjectTreeConfigState
      get() = service()
  }
}