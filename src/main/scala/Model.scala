package playground

import scalatags.JsDom.all._
import diode._

case class Model(active: SideBarItem, data: PageData)

case class PageData(
  topStories: List[Story],
  messages: List[Message],
  userPicture: String,
  counter: Counter
)

case class Story(
  id: Int,
  content: String,
  read: Boolean
)

case class MarkAsRead(id: Int) extends Action
case object AddStory extends Action

sealed trait SideBarItem {
  val label: String
}

object SideBarItem {
  val items: List[SideBarItem] = List(Home, Messages, Profile)
}

case object Home extends SideBarItem { val label = "Home" }
case object Messages extends SideBarItem { val label ="Messages" }
case object Profile extends SideBarItem {val label="Profile" } 

case class Message(title: String, text: String)

case class SidebarCmd(item: SideBarItem) extends Action
case object Initialize extends Action



case class Counter(count: Int)
case object Increment extends Action
case object Decrement extends Action

