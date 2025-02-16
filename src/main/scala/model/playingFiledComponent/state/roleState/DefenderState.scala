package model.playingFiledComponent.state.roleState

import model.playingFiledComponent.state.roleState.RoleState

class DefenderState extends RoleState {
  override def switchRoles(context: PlayerRoles): Unit = {
    println(s"${context.defender.name} is switching to Attacker!")
    context.setState(new AttackerState())
  }

  override def getRoleName: String = "Defender"
}
