package com.github.mallowigi.treehighlighter.models

import com.intellij.openapi.components.BaseState

class ColorSettings : BaseState() {
  val id: Int by property(0)

  val name: String? by string()

  val enabled: Boolean by property(true)
}