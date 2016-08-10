package com.hazelcast.simulator.protocol.processors;

import com.hazelcast.simulator.agent.workerprocess.WorkerProcessSettings;
import com.hazelcast.simulator.protocol.connector.CoordinatorConnector;
import com.hazelcast.simulator.protocol.core.AddressLevel;
import com.hazelcast.simulator.protocol.core.SimulatorAddress;
import com.hazelcast.simulator.protocol.operation.RemoteControllerOperation;
import com.hazelcast.simulator.protocol.registry.ComponentRegistry;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hazelcast.simulator.protocol.operation.RemoteControllerOperation.Type.INTEGRATION_TEST;
import static com.hazelcast.simulator.protocol.operation.RemoteControllerOperation.Type.LIST_COMPONENTS;
import static com.hazelcast.simulator.worker.WorkerType.CLIENT;
import static com.hazelcast.simulator.worker.WorkerType.MEMBER;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

public class CoordinatorRemoteControllerProcessorTest {

    private CoordinatorConnector serverConnector;
    private CoordinatorRemoteControllerProcessor remoteControllerProcessor;

    @Before
    public void setUp() {
        SimulatorAddress agent = new SimulatorAddress(AddressLevel.AGENT, 1, 0, 0);

        List<WorkerProcessSettings> settingsList = new ArrayList<WorkerProcessSettings>();
        settingsList.add(new WorkerProcessSettings(1, MEMBER, "outofthebox", "somescript", 0, new HashMap<String, String>()));
        settingsList.add(new WorkerProcessSettings(2, CLIENT, "outofthebox", "somescript", 0, new HashMap<String, String>()));

        serverConnector = mock(CoordinatorConnector.class);

        ComponentRegistry componentRegistry = new ComponentRegistry();
        componentRegistry.addAgent("127.0.0.1", "127.0.0.1");
        componentRegistry.addWorkers(agent, settingsList);

        remoteControllerProcessor = new CoordinatorRemoteControllerProcessor(serverConnector, componentRegistry);
    }

    @Test
    public void testProcess_withIntegrationTest() {
        remoteControllerProcessor.process(INTEGRATION_TEST);

        verifyZeroInteractions(serverConnector);
    }

    @Test
    public void testProcess_withListComponents() {
        remoteControllerProcessor.process(LIST_COMPONENTS);

        verify(serverConnector).writeToRemoteController(any(RemoteControllerOperation.class));
        verifyNoMoreInteractions(serverConnector);
    }
}
