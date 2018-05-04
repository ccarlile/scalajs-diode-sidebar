package playground

import scala.scalajs.js.JSApp

import org.scalajs.dom
import dom.{ document, window }
import scalatags.JsDom.all._
import diode._


class SidebarView(active: ModelRO[SideBarItem], dispatch: Dispatcher) {
  def render = {
    div(cls:="sidebar", SideBarItem.items.map {i: SideBarItem =>
          val activeCls = if (i == active.value) "active" else ""
          a(href:="#", cls:=activeCls, onclick := { () => dispatch(SidebarCmd(i)) }, i.label)
        }
    )
  }
}

class CounterView(counter: ModelRO[Counter], dispatch: Dispatcher) {
  def render = {
    div(cls:="counter",
        a(onclick := { () => dispatch(Decrement)}, "-"),
        p(counter.value.count),
        a(onclick := { () => dispatch(Increment)}, "+"))
  }
}

class HomeView(stories: ModelRO[List[Story]], dispatch: Dispatcher) {
  def render = {
    val storyDivs = stories.value.map { story =>
      val text = if (story.read) p(s(story.content)) else p(story.content)
      div(
        div(onclick:= { () => dispatch(MarkAsRead(story.id))}, text))
    }
    div(cls:="homeView", storyDivs, div(a(onclick:= { () => dispatch(AddStory)}, "Add a story")))
  }
}

class ActivePageView(model: ModelRO[Model], dispatch: Dispatcher) {
  val homeView = new HomeView(model.zoom(_.data.topStories), dispatch)
  def render = {
    model.value.active match {
      case Home => homeView.render
      case Messages => div("Messages")
      case Profile => div("Profile")
    }
  }
}

object MainApp extends JSApp {

  def main(): Unit = {
    println("Starting 'diode-playground'...")

    val sidebar = new SidebarView(SidebarCircuit.zoom(_.active), SidebarCircuit)
    val homePage = new HomeView(SidebarCircuit.zoom(_.data.topStories), SidebarCircuit)
    val activePage = new ActivePageView(SidebarCircuit.zoom(identity), SidebarCircuit)

    val sidebarRoot = document.getElementById("sidebarRoot")
    val pageviewRoot = document.getElementById("pageViewRoot")

    SidebarCircuit.subscribe(SidebarCircuit.zoom(identity))(_ => renderSidebar(sidebarRoot))
    // SidebarCircuit.subscribe(SidebarCircuit.zoom(_.data.topStories))(_ => renderHome())
    SidebarCircuit.subscribe(SidebarCircuit.zoom(identity))(_ => renderPage(pageviewRoot))
    SidebarCircuit(Initialize)

    def renderHome() = homePage.render

    def renderSidebar(root: dom.Element) = {
      root.innerHTML = ""
      root.appendChild(sidebar.render.render)
    }

    def renderPage(root: dom.Element) = {
      root.innerHTML = ""
      root.appendChild(activePage.render.render)
    }
  }
}

