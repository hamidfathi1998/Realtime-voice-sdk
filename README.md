## Session State Machine (Chapter 2)

```mermaid
stateDiagram-v2
    [*] --> Idle
    Idle --> Connecting
    Connecting --> Connected
    Connecting --> Disconnecting
    Connecting --> Failed
    Connected --> Listening
    Connected --> Speaking
    Connected --> Disconnecting
    Connected --> Failed
    Listening --> Speaking
    Listening --> Connected
    Listening --> Disconnecting
    Listening --> Failed
    Speaking --> Listening
    Speaking --> Connected
    Speaking --> Disconnecting
    Speaking --> Failed
    Disconnecting --> Idle
    Disconnecting --> Failed
    Failed --> Idle
    Failed --> Connecting
```

**Design notes**
- `kotlinx-coroutines-core` is exposed as `api` because `StateFlow`/`SharedFlow` are part of the public contract.
- Events use a hot `SharedFlow` without replay: events emitted with no collector are dropped (covered by tests).
- `fail()` and `reset()` intentionally bypass the transition table.
- Status: Chapter 2 delivers the core contract and state machine only. No networking or audio yet.