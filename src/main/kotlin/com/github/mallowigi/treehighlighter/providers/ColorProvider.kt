package com.github.mallowigi.treehighlighter.providers

import com.github.mallowigi.treehighlighter.services.AppTreeConfigState
import com.intellij.openapi.fileEditor.impl.EditorTabColorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.awt.Color

internal class ColorProvider : EditorTabColorProvider, DumbAware {
  override fun getEditorTabColor(project: Project, file: VirtualFile): Color? = getColor(project, file)

  override fun getProjectViewColor(project: Project, file: VirtualFile): Color? = getColor(project, file)

  private fun getColor(project: Project, file: VirtualFile): Color? {
    val colorSettings = AppTreeConfigState.instance.colorList
    println("ColorProvider.getColor: colorSettings = $colorSettings")

    return Color.BLACK
  }

}