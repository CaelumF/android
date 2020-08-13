package com.toggl.api.network.deserializers

import com.squareup.moshi.Moshi
import com.toggl.api.network.adapters.OffsetDateTimeAdapter
import com.toggl.api.network.models.sync.PushResponseJsonAdapter
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class PushResponseDeserializerTest {
    @Test
    fun `the push response is properly serialized`() {
        val moshi = Moshi.Builder().add(OffsetDateTimeAdapter()).build()
        val deserializedPushResponse = PushResponseJsonAdapter(moshi).fromJson(pushResponseJson)!!
        with(deserializedPushResponse) {

            clients shouldHaveSize 3
            clients[0].payload!!.success.shouldBeTrue()
            clients[1].payload!!.success.shouldBeTrue()
            clients[2].payload!!.success.shouldBeTrue()

            projects shouldHaveSize 3
            projects[0].payload!!.success.shouldBeTrue()
            projects[1].payload!!.success.shouldBeTrue()
            projects[2].payload!!.success.shouldBeTrue()

            tags shouldHaveSize 3
            tags[0].payload!!.success.shouldBeTrue()
            tags[1].payload!!.success.shouldBeTrue()
            tags[2].payload!!.success.shouldBeTrue()

            tasks shouldHaveSize 3
            tasks[0].payload!!.success.shouldBeTrue()
            tasks[1].payload!!.success.shouldBeTrue()
            tasks[2].payload!!.success.shouldBeTrue()

            timeEntries shouldHaveSize 3
            timeEntries[0].payload!!.success.shouldBeTrue()
            timeEntries[1].payload!!.success.shouldBeTrue()
            timeEntries[2].payload!!.success.shouldBeTrue()

            workspaces shouldHaveSize 2
            workspaces[0].payload!!.success.shouldBeTrue()
            workspaces[1].payload!!.success.shouldBeTrue()

            user.success.shouldBeTrue()
            preferences.success.shouldBeTrue()
        }
    }

    companion object {
        @Language("JSON")
        private const val pushResponseJson = """
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
    }
}