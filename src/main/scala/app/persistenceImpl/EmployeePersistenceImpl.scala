package app.persistenceImpl

import app.models.{Employee, EmployeeNotFound}
import app.persisatence.EmployeePersistence
import app.persisatence.EmployeePersistence.{DBTransactor, EmployeePersistence}
import cats.effect.Blocker
import doobie.h2.H2Transactor
import doobie.implicits._
import doobie.{Query0, Transactor, Update0}
import zio.interop.catz._
import zio.{Managed, Task, ZIO, ZLayer}

import scala.concurrent.ExecutionContext

class EmployeePersistenceImpl(tnx: Transactor[Task]) extends EmployeePersistence.Service {

  import EmployeePersistenceImpl._

  override def getById(id: Int): Task[Employee] =
    sql.getById(id).option.transact(tnx).foldM(
      err => Task.fail(err),
      emplOpt => Task.require(EmployeeNotFound(id))(Task.succeed(emplOpt))
    )

  override def create(employee: Employee): Task[Employee] =
    sql.create(employee).run.transact(tnx).foldM(
      err => Task.fail(err),
      _ => Task.succeed(employee)
    )

  override def deleteById(id: Int): Task[Boolean] =
    sql.deleteById(id).run.transact(tnx).fold(_ => false, _ => true)

  override def getAll: Task[Seq[Employee]] =
    sql.getAll.to[Seq].transact(tnx).foldM(
      err => Task.fail(err),
      res => Task.succeed(res)
    )
}

object EmployeePersistenceImpl {
  object sql {
    val employeeTableName = "employee"

    def getById(id: Int): Query0[Employee] =
      sql"select * from $employeeTableName where id = $id".query[Employee]

    def create(employee: Employee): Update0 =
      sql"insert into $employeeTableName values(${employee.fio}, ${employee.position})".update

    def deleteById(id: Int): Update0 =
      sql"delete from $employeeTableName where id = $id".update

    def getAll: Query0[Employee] =
      sql"select * from $employeeTableName".query[Employee]

    def createEmployeeTable: Update0 =
      sql"""
           create table $employeeTableName (
            id Int,
            fio varchar not null
            )
         """.update
  }

  def createEmployeeTable: ZIO[DBTransactor, Throwable, Unit] =
    for {
      tnx <- ZIO.service[Transactor[Task]]
      _ <- sql.createEmployeeTable.run.transact(tnx)
    } yield ()


  def mkTransactor(
                    connectEC: ExecutionContext,
                    transactEC: ExecutionContext
                  ): Managed[Throwable, Transactor[Task]] = {

    H2Transactor
      .newH2Transactor[Task](
        ???,
        "postgres",
        "",
        connectEC,
        Blocker.liftExecutionContext(transactEC)
      )
      .toManagedZIO
  }

  val live: ZLayer[DBTransactor, Throwable, EmployeePersistence] = ZLayer.fromService(new EmployeePersistenceImpl(_))
}