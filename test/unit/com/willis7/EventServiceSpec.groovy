package com.willis7

import grails.plugin.spock.*
import spock.lang.*

import org.joda.time.*

import com.willis7.EventService;
import com.willis7.Event;

import static org.joda.time.DateTimeConstants.*

@TestFor(EventService)
@Mock(Event)
class EventServiceSpec extends UnitSpec {

    @Shared DateTime now
    @Shared DateTime mondayNextWeek
    @Shared DateTime wednesdayNextWeek
    @Shared DateTime fridayNextWeek
    @Shared DateTime mondayAfterNext

    @Shared Event mwfEvent
    @Shared Event biWeeklyEvent


    def setupSpec() {
        now = new DateTime()
        mondayNextWeek = new DateTime().plusWeeks(1).withDayOfWeek(MONDAY).withTime(0,0,0,0)
        wednesdayNextWeek = mondayNextWeek.withDayOfWeek(WEDNESDAY)
        fridayNextWeek = mondayNextWeek.withDayOfWeek(FRIDAY)
        mondayAfterNext = mondayNextWeek.plusWeeks(1)

        mwfEvent = new Event(
                title: 'Repeating MWF Event',
                startTime: mondayNextWeek.toDate(),
                endTime: mondayNextWeek.plusHours(1).toDate(),
                location: "Regular location",
                recurType: EventRecurType.WEEKLY,
                isRecurring: true,
                recurDaysOfWeek: [MONDAY, WEDNESDAY, FRIDAY]
        )

        biWeeklyEvent = new Event(mwfEvent.properties)
        biWeeklyEvent.recurInterval = 2
    }

    @Unroll("next occurance of weekly event after #afterDate")
    def "next occurrence of a weekly event without excluded days"() {
        expect:
            service.findNextOccurrence(event, afterDate.toDate()) == expectedResult.toDate()

        where:
            event    | afterDate         | expectedResult
            mwfEvent | now               | mondayNextWeek
            mwfEvent | mondayNextWeek    | wednesdayNextWeek
            mwfEvent | wednesdayNextWeek | fridayNextWeek
    }

    @Unroll("next occurence of bi-weekly event after #afterDate")
    def "next occurrence of a bi-weekly event"() {
        expect:
        service.findNextOccurrence(event, afterDate.toDate()) == expectedResult.toDate()

        where:
        event         | afterDate           | expectedResult
        biWeeklyEvent | now                 | mondayNextWeek
        biWeeklyEvent | mondayNextWeek      | wednesdayNextWeek
        biWeeklyEvent | wednesdayNextWeek   | fridayNextWeek
        biWeeklyEvent | fridayNextWeek      | mondayNextWeek.plusWeeks(2)
        biWeeklyEvent | mondayAfterNext     | mondayNextWeek.plusWeeks(2)
    }


    @Unroll("next occurence of weekly event after #afterDate with exclusion")
    def "next occurrence of a weekly event with an exclusion of next monday"() {
        setup:
            mwfEvent.addToExcludeDays(mondayNextWeek.toDate())

        expect:
            service.findNextOccurrence(event, afterDate.toDate()) == expectedResult.toDate()


        where:
            event    | afterDate           | expectedResult
            mwfEvent | now                 | wednesdayNextWeek
            mwfEvent | mondayNextWeek      | wednesdayNextWeek
            mwfEvent | wednesdayNextWeek   | fridayNextWeek
    }

    def "deleting an instance of a daily recurring event"() {
        def event = new Event(
                title: 'Repeating MWF Event',
                startTime: mondayNextWeek.toDate(),
                endTime: mondayNextWeek.plusHours(1).toDate(),
                location: "Regular location",
                recurType: EventRecurType.DAILY,
                isRecurring: true
        )

        when:
        event.addToExcludeDays(mondayNextWeek.toDate())

        then:
        service.findNextOccurrence(event, mondayNextWeek.minusDays(1).toDate()) == mondayNextWeek.plusDays(1).toDate()
    }


    def "deleting an instance of a bi-daily recurring event"() {
        def event = new Event(
                title: 'Repeating MWF Event',
                startTime: mondayNextWeek.toDate(),
                endTime: mondayNextWeek.plusHours(1).toDate(),
                location: "Regular location",
                recurInterval: 2,
                recurType: EventRecurType.DAILY,
                isRecurring: true
        )

        when:
        event.addToExcludeDays(mondayNextWeek.toDate())

        then:
        service.findNextOccurrence(event, mondayNextWeek.minusDays(1).toDate()) == mondayNextWeek.plusDays(2).toDate()
    }

    @Unroll("next occurence of daily event after #afterDate")
    def "repeating daily event"() {
        def event = new Event(
                title: 'Repeating Daily Event',
                startTime: now.withTime(0, 0, 0, 0).toDate(),
                endTime: now.withTime(0, 0, 0, 0).plusHours(1).toDate(),
                location: "Regular location",
                recurInterval: 1,
                recurType: EventRecurType.DAILY,
                isRecurring: true
        )

        expect:
        service.findNextOccurrence(event, afterDate.toDate()) == afterDate.plusDays(1).withTime(0,0,0,0).toDate()


        where:
        afterDate << [now, mondayNextWeek, wednesdayNextWeek]
    }

    @Unroll("next occurrence of event every other day after #afterDate")
    def "repeating every other day event"() {
        def event = new Event(
                title: 'Repeating Daily Event',
                startTime: mondayNextWeek.withTime(0, 0, 0, 0).toDate(),
                endTime: mondayNextWeek.withTime(0, 0, 0, 0).plusHours(1).toDate(),
                location: "Regular location",
                recurInterval: 2,
                recurType: EventRecurType.DAILY,
                isRecurring: true
        )

        expect:
        service.findNextOccurrence(event, date) == expectedResult


        where:
        date                                | expectedResult
        mondayNextWeek.toDate()             | mondayNextWeek.plusDays(2).toDate()
        mondayNextWeek.plusDays(1).toDate() | mondayNextWeek.plusDays(2).toDate()
        wednesdayNextWeek.toDate()          | wednesdayNextWeek.plusDays(2).toDate()

    }






}
