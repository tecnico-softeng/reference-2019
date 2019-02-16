package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ist.fenixframework.FenixFramework
import pt.ist.fenixframework.core.WriteOnReadError
import spock.lang.Specification

import javax.transaction.NotSupportedException
import javax.transaction.SystemException

abstract class SpockRollbackClassLevelTestAbstractClass extends Specification implements SharedDefinitions {

	def setupSpec() throws Exception {
		try {
			FenixFramework.getTransactionManager().begin(false)
			populate4Test()
		} catch (WriteOnReadError | NotSupportedException | SystemException e1) {
			e1.printStackTrace()
		}
	}

	def cleanupSpec() {
		try {
			FenixFramework.getTransactionManager().rollback()
		} catch (IllegalStateException | SecurityException | SystemException e) {
			e.printStackTrace()
		}
	}

	abstract def populate4Test()
}
