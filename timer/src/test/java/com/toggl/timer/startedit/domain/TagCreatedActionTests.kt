package com.toggl.timer.startedit.domain

import com.toggl.models.domain.EditableTimeEntry
import com.toggl.models.domain.Tag
import com.toggl.timer.common.testReduceState
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("The TagCreated Action")
class TagCreatedActionTests {
    val initialState = createInitialState()
    val reducer = createReducer()

    @Nested
    @DisplayName("When no tags have already been added to the state")
    inner class NoTagsInPlace {
        @Test
        fun `Adds the created tag to the editableTimeEntry's tagIds`() = runBlockingTest {
            val editableTimeEntry = EditableTimeEntry.empty(1)
            val initialState = initialState.copy(
                editableTimeEntry = editableTimeEntry
            )
            val newTag = Tag(1, "Tag", 1)

            reducer.testReduceState(
                initialState,
                StartEditAction.TagCreated(newTag)
            ) {
                it shouldBe initialState.copy(
                    editableTimeEntry = editableTimeEntry.copy(
                        tagIds = listOf(newTag.id)
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("When tags have already been added to the state")
    inner class WhenSomeTagsAreAlreadyInPlace {
        @Test
        fun `Adds the created tag to the editableTimeEntry's tagIds`() = runBlockingTest {
            val existingTags = (0..10L).map { Tag(it, "#$it", 1) }
            val editableTimeEntry = EditableTimeEntry.empty(1).copy(
                tagIds = listOf(1L, 2L)
            )
            val initialState = initialState.copy(
                tags = existingTags.associateBy { it.id },
                editableTimeEntry = editableTimeEntry
            )
            val newTag = Tag(20L, "MuchTag", 1)

            reducer.testReduceState(
                initialState,
                StartEditAction.TagCreated(newTag)
            ) {
                it shouldBe initialState.copy(
                    editableTimeEntry = editableTimeEntry.copy(
                        tagIds = listOf(1L, 2L, 20L)
                    )
                )
            }
        }
    }
}
