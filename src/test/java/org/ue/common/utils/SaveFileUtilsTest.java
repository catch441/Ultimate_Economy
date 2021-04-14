package org.ue.common.utils;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SaveFileUtilsTest {

	@InjectMocks
	SaveFileUtils utils;
	
	@Test
	public void saveErrorTest() throws IOException {
		YamlConfiguration config = mock(YamlConfiguration.class);
		File savefile = mock(File.class);
		IOException e = mock(IOException.class);
		when(e.getMessage()).thenReturn("my error");
		doThrow(e).when(config).save(savefile);
		utils.save(config, savefile);
		verify(e).getMessage();
	}
	
	@Test
	public void createFileErrorTest() throws IOException {
		File savefile = mock(File.class);
		IOException e = mock(IOException.class);
		when(e.getMessage()).thenReturn("my error");
		doThrow(e).when(savefile).createNewFile();
		utils.createFile(savefile);
		verify(e).getMessage();
	}
}
