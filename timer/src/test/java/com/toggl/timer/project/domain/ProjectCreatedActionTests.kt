package com.toggl.timer.project.domain

import com.google.common.truth.Truth.assertThat
import com.toggl.models.domain.EditableProject
import com.toggl.models.domain.EditableTimeEntry
import com.toggl.timer.common.CoroutineTest
import com.toggl.timer.common.testReduceEffects
import com.toggl.timer.common.testReduceState

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@DisplayName("The ProjectCreated action")
internal class ProjectCreatedActionTests : CoroutineTest() {
    private val reducer = createProjectReducer(dispatcherProvider = dispatcherProvider)
    private val listOfProjects = listOf(
        createProject(1, "Project 1", clientId = 1, workspaceId = 1),
        createProject(2, "Project 2", clientId = 1, workspaceId = 1),
        createProject(3, "Project 3", clientId = 1, workspaceId = 1)
    )

    @Test
    fun `updates the time entry description`() = runBlockingTest {

        val initialState = createInitialState(
            EditableProject.createValidBecauseClientsAreDifferent(),
            listOfProjects,
            editableTimeEntry = EditableTimeEntry.empty(1).copy(description = "Test @Proj")
        )
        val newProject = createProject(4, "Project 4")

        reducer.testReduceState(
            initialState = initialState,
            action = ProjectAction.ProjectCreated(newProject)
        ) { state -> assertThat(state.editableTimeEntry.description).isEqualTo("Test ") }
    }

    @Test
    fun `updates the time entry's project id`() = runBlockingTest {

        val initialState = createInitialState(EditableProject.createValidBecauseClientsAreDifferent(), listOfProjects)
        val newProject = createProject(4, "Project 4")

        reducer.testReduceState(
            initialState = initialState,
            action = ProjectAction.ProjectCreated(newProject)
        ) { state -> assertThat(state.editableTimeEntry.projectId).isEqualTo(newProject.id) }
    }

    @Test
    fun `returns an effect to close the view`() = runBlockingTest {

        val initialState = createInitialState(EditableProject.createValidBecauseClientsAreDifferent(), listOfProjects)
        val newProject = createProject(4, "Project 4")

        reducer.testReduceEffects(initialState = initialState, action = ProjectAction.ProjectCreated(newProject)) { effects ->
            val closeAction = effects.single().execute() as? ProjectAction.Close
            assertThat(closeAction).isNotNull()
        }
    }
}