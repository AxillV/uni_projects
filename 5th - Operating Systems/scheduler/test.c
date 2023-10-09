void staticRun(process *processToRun){
	int pid = fork();

	if (pid == 0) {
		char *fullpath = processToRun->fullpath;
		// https://stackoverflow.com/questions/26453624/hide-terminal-output-from-execve

		// opening dev/null
		// ???: IF ANY BUGS ARISE SCRAP THIS
		int fd = open("/dev/null", O_WRONLY);
		dup2(fd, 1);
		dup2(fd, 2);
		close(fd);

		execl(fullpath, fullpath, NULL);
		exit(127); //command not found
	}else{
		double processStartTime = get_wtime();

		processToRun->status = RUNNING;
		waitpid(pid, NULL, 0);
		processToRun->status = EXITED;

		double processEndTime = get_wtime();
		double elapsedTime = processEndTime - processStartTime;
		double workloadTime = processEndTime - executionStartTime;
		processToRun->timeUsed += elapsedTime;

		printf("PID %d - CMD %s\n", pid, basename(processToRun->fullpath));
		printf("\t\t\tElapsed Time: %lf\n", elapsedTime);
		printf("\t\t\tWorkload Time: %lf\n", workloadTime);
	}
}