package model.playingFiledComponent.roleState

class DefenderState extends RoleState {
  override def switchRoles(context: PlayerRoles): Unit = {
    println(s"${context.defender.name} is switching to Attacker!")
    context.setState(new AttackerState())
  }

  override def getRoleName: String = "Defender"
}
