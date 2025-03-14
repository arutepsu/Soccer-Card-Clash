package model.playerComponent.playerRole

import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField

trait IRolesManager {
    def attacker: IPlayer
    def defender: IPlayer
    def switchRoles(): Unit
    def setRoles(newAttacker: IPlayer, newDefender: IPlayer): Unit
    def reset(): Unit
}
