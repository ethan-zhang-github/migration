package priv.ethanzhang.migration.example.event;

import org.junit.Test;
import priv.ethanzhang.migration.core.event.*;
import priv.ethanzhang.migration.core.reader.EasyExcelMigrationReader;
import priv.ethanzhang.migration.core.reader.MigrationReader;
import priv.ethanzhang.migration.core.utils.GenericUtil;

import java.io.File;

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

        EasyExcelMigrationReader<Integer> reader1 = new EasyExcelMigrationReader<Integer>(new File("")) {};

        EasyExcelMigrationReader<Integer> reader2 = new EasyExcelMigrationReader<>(new File(""));

        System.out.println(GenericUtil.getInterfaceGenericType(reader1.getClass(), MigrationReader.class, 0));

    }

}
