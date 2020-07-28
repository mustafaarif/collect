package org.odk.collect.android.formmanagement;

import org.junit.Test;
import org.odk.collect.android.forms.Form;
import org.odk.collect.android.instances.Instance;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.support.InMemFormsRepository;
import org.odk.collect.android.support.InMemInstancesRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class FormDeleterTest {

    @Test
    public void whenFormHasSubmittedInstances_deletesForm() {
        InMemFormsRepository formsRepository = new InMemFormsRepository();
        InMemInstancesRepository instancesRepository = new InMemInstancesRepository();
        FormDeleter formDeleter = new FormDeleter(formsRepository, instancesRepository);

        formsRepository.save(new Form.Builder()
                .id(1L)
                .jrFormId("1")
                .build());

        instancesRepository.addInstance(new Instance.Builder()
                .jrFormId("1")
                .status(InstanceProviderAPI.STATUS_SUBMITTED)
                .build());

        formDeleter.delete(1L);
        assertThat(formsRepository.getAll().isEmpty(), is(true));
    }

    @Test
    public void whenOtherVersionOfFormHasInstances_deletesForm() {
        InMemFormsRepository formsRepository = new InMemFormsRepository();
        InMemInstancesRepository instancesRepository = new InMemInstancesRepository();
        FormDeleter formDeleter = new FormDeleter(formsRepository, instancesRepository);

        formsRepository.save(new Form.Builder()
                .id(1L)
                .jrFormId("1")
                .jrVersion("old")
                .build());

        formsRepository.save(new Form.Builder()
                .id(2L)
                .jrFormId("1")
                .jrVersion("new")
                .build());

        instancesRepository.addInstance(new Instance.Builder()
                .jrFormId("1")
                .jrVersion("old")
                .status(InstanceProviderAPI.STATUS_COMPLETE)
                .build());

        formDeleter.delete(2L);
        List<Form> forms = formsRepository.getAll();
        assertThat(forms.size(), is(1));
        assertThat(forms.get(0).getJrVersion(), is("old"));
    }
}