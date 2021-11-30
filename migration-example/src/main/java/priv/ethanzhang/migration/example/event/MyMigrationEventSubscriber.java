package priv.ethanzhang.migration.example.event;

import org.junit.Test;
import priv.ethanzhang.migration.core.event.*;

public class MyMigrationEventSubscriber implements MigrationEventSubscriber<MigrationTaskLifecycleEvent> {

    @Override
    public void subscribe(MigrationTaskLifecycleEvent event) {
        System.out.println("...");
    }

    @Test
    public void test() {
        MyMigrationEventSubscriber subscriber = new MyMigrationEventSubscriber();
        LocalMigrationEventDispatcher.INSTANCE.addSubsriber(subscriber);
        LocalMigrationEventDispatcher.INSTANCE.dispatch(new MigrationTaskFailedEvent(null, null, null));
        LocalMigrationEventDispatcher.INSTANCE.dispatch(new MigrationTaskWarnningEvent(null, null, null));
    }

}
