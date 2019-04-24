package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class IRSTaxesMethodSpockTest extends SpockRollbackTestAbstractClass {
    def NIF_ONE = '123456789'
    def NIF_TWO = '987654321'
    def FOOD = 'FOOD'
    def TAX = 10
    def date = new LocalDate(2018, 02, 13)
    def irs
    def taxPayerOne
    def taxPayerTwo
    def itemType

    @Override
    def populate4Test() {
        irs = IRS.getIRSInstance()

        taxPayerOne = new TaxPayer(irs, NIF_ONE, 'José Vendido', 'Somewhere')
        taxPayerTwo = new TaxPayer(irs, NIF_TWO, 'Manuel Comprado', 'Anywhere')
        itemType = new ItemType(irs, FOOD, TAX)
    }

    def 'success'() {
        given:
        new Invoice(100 * IRS.SCALE, date, itemType, taxPayerOne, taxPayerTwo)
        new Invoice(100 * IRS.SCALE, date, itemType, taxPayerTwo, taxPayerOne)
        new Invoice(50 * IRS.SCALE, date, itemType, taxPayerTwo, taxPayerOne)

        when:
        def value = irs.taxes(year)

        then:
        toPay == value / IRS.SCALE

        where:
        year | toPay
        2018 | 25.0
        2015 | 0

    }

    def 'no invoices'() {
        expect:
        def value = taxPayerOne.toPay(2018)
        0.00f == value
    }

    def 'before 1970'() {
        when:
        new Invoice(100 * IRS.SCALE, new LocalDate(1969, 02, 13), itemType, taxPayerOne, taxPayerTwo)
        new Invoice(50 * IRS.SCALE, new LocalDate(1969, 02, 13), itemType, taxPayerOne, taxPayerTwo)

        taxPayerOne.toPay(1969)

        then:
        thrown(TaxException)
    }

    def 'equal 1970'() {
        when:
        new Invoice(100 * IRS.SCALE, new LocalDate(1970, 02, 13), itemType, taxPayerOne, taxPayerTwo)
        new Invoice(50 * IRS.SCALE, new LocalDate(1970, 02, 13), itemType, taxPayerOne, taxPayerTwo)

        def value = taxPayerOne.toPay(1970)

        then:
        15.0 == value / IRS.SCALE
    }

    def 'ignore cancelled'() {
        when:
        new Invoice(100 * IRS.SCALE, date, itemType, taxPayerOne, taxPayerTwo)
        def invoice = new Invoice(100 * IRS.SCALE, date, itemType, taxPayerOne, taxPayerTwo)

        new Invoice(50 * IRS.SCALE, date, itemType, taxPayerOne, taxPayerTwo)
        invoice.cancel()

        def value = taxPayerOne.toPay(2018)

        then:
        15.0 == value / IRS.SCALE
    }

}
