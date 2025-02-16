package model.playingFiledComponent.state.roleState

import model.playingFiledComponent.state.roleState.PlayerRoles

trait RoleState {
  def switchRoles(context: PlayerRoles): Unit
  def getRoleName: String
}

