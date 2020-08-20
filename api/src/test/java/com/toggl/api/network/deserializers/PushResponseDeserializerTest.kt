package com.toggl.api.network.deserializers

import com.squareup.moshi.Moshi
import com.toggl.api.network.adapters.DateAdapter
import com.toggl.api.network.adapters.OffsetDateTimeAdapter
import com.toggl.api.network.models.sync.ActionResult
import com.toggl.api.network.models.sync.PushResponseJsonAdapter
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

@Suppress("UNCHECKED_CAST")
class PushResponseDeserializerTest {

    private val moshi = Moshi.Builder()
        .add(OffsetDateTimeAdapter())
        .add(DateAdapter())
        .build()

    @Test
    fun `the push response is properly serialized`() {
        val deserializedPushResponse = PushResponseJsonAdapter(moshi).fromJson(pushResponseJson)!!
        with(deserializedPushResponse) {

            clients shouldHaveSize 3
            clients.all { it.payload is ActionResult.SuccessResult }.shouldBeTrue()

            projects shouldHaveSize 3
            projects.all { it.payload is ActionResult.SuccessResult }.shouldBeTrue()

            tags shouldHaveSize 3
            tags.all { it.payload is ActionResult.SuccessResult }.shouldBeTrue()

            tasks shouldHaveSize 3
            tasks.all { it.payload is ActionResult.SuccessResult }.shouldBeTrue()

            timeEntries shouldHaveSize 3
            timeEntries.all { it.payload is ActionResult.SuccessResult }.shouldBeTrue()

            workspaces shouldHaveSize 2
            workspaces.all { it.payload is ActionResult.SuccessResult }.shouldBeTrue()

            (user is ActionResult.SuccessResult).shouldBeTrue()
            (preferences is ActionResult.SuccessResult).shouldBeTrue()
        }
    }

    @Test
    fun `the push response is properly deserialized when it contains errors`() {
        val deserializedPushResponse = PushResponseJsonAdapter(moshi).fromJson(pushResponseWithErrorsJson)!!
        with(deserializedPushResponse) {
            clients shouldHaveSize 3
            clients.all { it.payload is ActionResult.ErrorResult }.shouldBeTrue()
        }
    }

    companion object {
        @Language("JSON")
        private const val pushResponseJson =
            """
        {
            "clients": [
                {
                    "type": "create",
                    "meta": {"client_assigned_id": "-123"},
                    "payload": {
                        "success": true,
                        "result": {
                            "id": 456,
                            "name": "Client A",
                            "workspace_id": 789
                        }
                    }
                },
                {
                    "type": "update",
                    "meta": {"id": "456"},
                    "payload": {
                        "success": true,
                        "result": {
                            "id": 456,
                            "name": "Client A",
                            "workspace_id": 789
                        }
                    }
                },
                {
                    "type": "delete",
                    "meta": {"id": "789"},
                    "payload": { "success": true }
                }
            ],
            "projects": [
                {
                    "type": "create",
                    "meta": {"client_assigned_id": "-123"},
                    "payload": {
                        "success": true,
                        "result": {
                            "id": 456,
                            "name": "Project A",
                            "workspace_id": 789
                        }
                    }
                },
                {
                    "type": "update",
                    "meta": {"id": "456"},
                    "payload": {
                        "success": true,
                        "result": {
                            "id": 456,
                            "name": "Project A",
                            "workspace_id": 789
                        }
                    }
                },
                {
                    "type": "delete",
                    "meta": {"id": "789"},
                    "payload": { "success": true }
                }
            ],
            "tags": [
                {
                    "type": "create",
                    "meta": {"client_assigned_id": "-123"},
                    "payload": {
                        "success": true,
                        "result": {
                            "id": 456,
                            "name": "Tag A",
                            "workspace_id": 789
                        }
                    }
                },
                {
                    "type": "update",
                    "meta": {"id": "456"},
                    "payload": {
                        "success": true,
                        "result": {
                            "id": 456,
                            "name": "Tag A",
                            "workspace_id": 789
                        }
                    }
                },
                {
                    "type": "delete",
                    "meta": {"id": "789"},
                    "payload": { "success": true }
                }
            ],
            "tasks": [
                {
                    "type": "create",
                    "meta": {"client_assigned_id": "-123"},
                    "payload": {
                        "success": true,
                        "result": {
                            "id": 456,
                            "name": "Task A",
                            "workspace_id": 789,
                            "project_id": 123
                        }
                    }
                },
                {
                    "type": "update",
                    "meta": {"id": "456"},
                    "payload": {
                        "success": true,
                        "result": {
                            "id": 456,
                            "name": "Task A",
                            "workspace_id": 789,
                            "project_id": 123
                        }
                    }
                },
                {
                    "type": "delete",
                    "meta": {"id": "789"},
                    "payload": { "success": true }
                }
            ],
            "time_entries": [
                {
                    "type": "create",
                    "meta": {"client_assigned_id": "-123"},
                    "payload": {
                        "success": true,
                        "result": {
                            "id": 456,
                            "description": "Time Entry A",
                            "workspace_id": 789
                        }
                    }
                },
                {
                    "type": "update",
                    "meta": {"id": "456"},
                    "payload": {
                        "success": true,
                        "result": {
                            "id": 456,
                            "description": "Time Entry A",
                            "workspace_id": 789
                        }
                    }
                },
                {
                    "type": "delete",
                    "meta": {"id": "789"},
                    "payload": { "success": true }
                }
            ],
            "workspaces": [
                {
                    "type": "create",
                    "meta": {"client_assigned_id": "-123"},
                    "payload": {
                        "success": true,
                        "result": {
                            "id": 456,
                            "name": "Workspace A"
                        }
                    }
                },
                {
                    "type": "update",
                    "meta": {"id": "456"},
                    "payload": {
                        "success": true,
                        "result": {
                            "id": 456,
                            "name": "Workspace A"
                        }
                    }
                }
            ],
            "user": {
                "success": true,
                "result": {
                    "fullname": "User A"
                }
            },
            "preferences": {
                "success": true,
                "result": {
                    "duration_format": "classic"
                }
            }
        }"""

        @Language("JSON")
        private const val pushResponseWithErrorsJson =
            """
        {
            "projects": [],
            "tasks": [],
            "tags": [],
            "workspaces": [],
            "time_entries": [],
            "user": {
                "success": true,
                "result": {
                    "fullname": "User A"
                }
            },
            "preferences": {
                "success": true,
                "result": {
                    "duration_format": "classic"
                }
            },
            "clients": [
                {
                    "type": "create",
                    "meta": {"client_assigned_id": "-123"},
                    "payload": {
                        "success": false,
                        "result": {
                            "error_message": {
                                "code": 123,
                                "default_message": "Creating failed."
                            }
                        }
                    }
                },
                {
                    "type": "update",
                    "meta": {"id": "456"},
                    "payload": {
                        "success": false,
                        "result": {
                            "error_message": {
                                "code": 456,
                                "default_message": "Updating failed."
                            }
                        }
                    }
                },
                {
                    "type": "delete",
                    "meta": {"id": "789"},
                    "payload": {
                        "success": false,
                        "result": {
                            "error_message": {
                                "code": 789,
                                "default_message": "Deleting failed."
                            }
                        }
                    }
                }
            ]
        }"""
    }
}
