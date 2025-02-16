package model.playingFiledComponent.roleState

trait RoleState {
  def switchRoles(context: PlayerRoles): Unit
  def getRoleName: String
}

