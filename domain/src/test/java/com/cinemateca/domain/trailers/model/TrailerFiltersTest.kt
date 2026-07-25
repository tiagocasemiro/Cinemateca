package com.cinemateca.domain.trailers.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class TrailerFiltersTest {
    @Test
    fun `uses KinoCheck pagination defaults`() {
        val filters = TrailerFilters()

        assertEquals(1, filters.page)
        assertEquals(25, filters.limit)
    }

    @Test
    fun `rejects pagination outside KinoCheck limits`() {
        assertThrows(IllegalArgumentException::class.java) {
            TrailerFilters(page = 0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            TrailerFilters(limit = 101)
        }
    }
}
