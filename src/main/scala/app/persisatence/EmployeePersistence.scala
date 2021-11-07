package app.persisatence

import app.models.Employee
import doobie.util.transactor.Transactor
import zio.{Has, RIO, Task}

object EmployeePersistence {

  trait Service {
    def getById(id: Int): Task[Employee]
    def create(employee: Employee): Task[Employee]
    def deleteById(id: Int): Task[Boolean]
    def getAll: Task[Seq[Employee]]
  }

  type DBTransactor    = Has[Transactor[Task]]
  type EmployeePersistence = Has[Service]

  def getById(id: Int):  RIO[EmployeePersistence, Employee] = RIO.accessM(_.get.getById(id))
  def create(employee: Employee): RIO[EmployeePersistence, Employee] = RIO.accessM(_.get.create(employee))
  def deleteById(id: Int): RIO[EmployeePersistence, Boolean] = RIO.accessM(_.get.deleteById(id))
  def getAll: RIO[EmployeePersistence, Seq[Employee]] = RIO.accessM(_.get.getAll)
}
