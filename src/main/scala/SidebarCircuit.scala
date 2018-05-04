package playground

import diode._

object SidebarCircuit extends Circuit[Model] {
  def initialModel = Model(Home, PageData(Nil, Nil, "", Counter(0)))

  val counterHandler = new ActionHandler(zoomTo(_.data.counter.count)) {
    override def handle = {
      case Increment => updated(value + 1)
      case Decrement => updated(value - 1)
    }
  }

  val homeHandler = new ActionHandler(zoomTo(_.data.topStories)) {
    def getId(stories: List[Story]) = stories match {
      case Nil => 1
      case _ => stories.map(_.id).max + 1
    }

    override def handle = {
      case MarkAsRead(id) => updated(value.map(s => if(s.id == id) s.copy(read=true) else s))
      case AddStory => {
        val stories: List[Story] = value
        val newStory = Story(getId(stories), "Here's a new story", false)
        updated(newStory :: value)
      }
    }
  }

  val sidebarHandler = new ActionHandler(zoomTo(_.active)) {
    override def handle = {
      case SidebarCmd(Home) => updated(Home)
      case SidebarCmd(Messages) => updated(Messages)
      case SidebarCmd(Profile) => updated(Profile)
      case Initialize => updated(Home)
    }
  }

  val actionHandler = composeHandlers(sidebarHandler, counterHandler, homeHandler)
}
