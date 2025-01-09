package org.limckmy.account.configuration;

import org.limckmy.account.dto.AccountBatchDTO;
import org.limckmy.account.entity.Account;
import org.limckmy.account.entity.Customer;
import org.limckmy.account.repository.AccountRepository;
import org.limckmy.account.repository.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    // Account Processor to associate Customer with Account
    @Bean
    public ItemProcessor<AccountBatchDTO, Account> accountProcessor(CustomerRepository customerRepository) {
        return accountDTO -> {
            Account account = new Account();
            account.setAccountId(accountDTO.getAccountId());
            account.setAccountNumber(accountDTO.getAccountNumber());
            account.setAccountType(accountDTO.getAccountType());
            account.setAccountDescription(accountDTO.getAccountDescription());
            account.setBalance(accountDTO.getBalance());

            // Fetch the customer by ID for the current account
            Customer customer = customerRepository.findById(accountDTO.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + accountDTO.getCustomerId()));

            account.setCustomer(customer);  // Set the customer reference on account
            return account;
        };
    }


    @Bean
    public FlatFileItemReader<Customer> csvReaderCustomer() {
        return createCsvReader("customer.csv", Customer.class, "customerId", "name", "email", "phone");
    }

    @Bean
    public FlatFileItemReader<AccountBatchDTO> csvReaderAccount() {
        return createCsvReader("account.csv", AccountBatchDTO.class, "accountId", "accountNumber", "accountType", "accountDescription", "balance", "customerId");
    }

    /**
     * A reusable method for creating a FlatFileItemReader for any CSV file and target type.
     *
     * @param fileName      The name of the CSV file in the classpath.
     * @param targetType    The class type of the objects to map to (e.g., Customer or Account).
     * @param fieldNames    The names of the fields to map from the CSV to the target class.
     * @return The configured FlatFileItemReader.
     */
    private <T> FlatFileItemReader<T> createCsvReader(String fileName, Class<T> targetType, String... fieldNames) {
        FlatFileItemReader<T> reader = new FlatFileItemReaderBuilder<T>()
                .name(fileName + "Reader")
                .resource(new ClassPathResource(fileName))
                .delimited()
                .names(fieldNames)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(targetType);
                }})
                .build();

        // Skip the first line (header row)
        reader.setLinesToSkip(1);
        return reader;
    }

    @Bean
    public RepositoryItemWriter<Customer> customerWriter(CustomerRepository customerRepository) {
        return new RepositoryItemWriterBuilder<Customer>()
                .repository(customerRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public RepositoryItemWriter<Account> accountWriter(AccountRepository accountRepository) {
        return new RepositoryItemWriterBuilder<Account>()
                .repository(accountRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step csvCustomerProcessingStep(FlatFileItemReader<Customer> csvReaderCustomer,
                                  RepositoryItemWriter<Customer> customerWriter) {
        return new StepBuilder("csvCustomerProcessingStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(csvReaderCustomer)
                .writer(customerWriter)
                .build();
    }


    @Bean
    public Step csvAccountProcessingStep(FlatFileItemReader<AccountBatchDTO> csvReaderAccount,
                                         ItemProcessor<AccountBatchDTO, Account> accountProcessor,
                                         RepositoryItemWriter<Account> accountWriter) {
        return new StepBuilder("csvAccountProcessingStep", jobRepository)
                .<AccountBatchDTO, Account>chunk(10, transactionManager)
                .reader(csvReaderAccount)
                .processor(accountProcessor)
                .writer(accountWriter)
                .build();
    }

    @Bean
    public Job csvProcessingJob(Step csvCustomerProcessingStep, Step csvAccountProcessingStep) {
        return new JobBuilder("csvProcessingJob", jobRepository)
                .start(csvCustomerProcessingStep)
                .next(csvAccountProcessingStep)
                .build();
    }
}
