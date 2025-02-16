package model.playingFiledComponent.state.roleState

import model.playingFiledComponent.state.roleState.RoleState

class AttackerState extends RoleState {
  override def switchRoles(context: PlayerRoles): Unit = {
    println(s"${context.attacker.name} is switching to Defender!")
    context.setState(new DefenderState())
  }

  override def getRoleName: String = "Attacker"
}
