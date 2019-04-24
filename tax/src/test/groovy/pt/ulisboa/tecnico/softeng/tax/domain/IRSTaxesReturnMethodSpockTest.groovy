package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Unroll

class IRSTaxesReturnMethodSpockTest extends SpockRollbackTestAbstractClass {
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

    @Unroll('testing success: #year, #val')
    def 'success'() {
        given:
        new Invoice(100 * IRS.SCALE, date, itemType, taxPayerOne, taxPayerTwo)
        new Invoice(100 * IRS.SCALE, date, itemType, taxPayerTwo, taxPayerOne)
        new Invoice(50 * IRS.SCALE, date, itemType, taxPayerTwo, taxPayerOne)

        when:
        def value = irs.taxesReturn(year)

        then:
        val == value

        where:
        year | val
        2018 | 1250
        2017 | 0.00f
    }


    def 'no invoices'() {
        when:
        def value = taxPayerTwo.taxReturn(2018)

        then:
        0.00f == value
    }

    def 'before 1970'() {
        when:
        new Invoice(100 * IRS.SCALE, new LocalDate(1969, 02, 13), itemType, taxPayerOne, taxPayerTwo)

        then:
        thrown(TaxException)
    }

    def 'equal 1970'() {
        given:
        new Invoice(100 * IRS.SCALE, new LocalDate(1970, 02, 13), itemType, taxPayerOne, taxPayerTwo)

        when:
        def value = taxPayerTwo.taxReturn(1970)

        then:
        500 == value
    }

    def 'ignore cancelled'() {
        given:
        new Invoice(100 * IRS.SCALE, date, itemType, taxPayerOne, taxPayerTwo)
        def invoice = new Invoice(100 * IRS.SCALE, date, itemType, taxPayerOne, taxPayerTwo)
        new Invoice(50 * IRS.SCALE, date, itemType, taxPayerOne, taxPayerTwo)

        invoice.cancel()

        when:
        def value = taxPayerTwo.taxReturn(2018)

        then:
        750 == value
    }

}
